package com.thaiopensource.xml.infer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.thaiopensource.xml.util.Name;

class ContentModelInferrerImpl extends ContentModelInferrer
{
  private static final Name START = new Name ("", "#start");
  private static final Name END = new Name ("", "#end");

  /**
   * Maps names to nodes.
   */
  private final Map <Name, SingleNode> nameMap = new HashMap <Name, SingleNode> ();

  private SingleNode prevNode;
  private final SingleNode startNode;
  private final SingleNode endNode;

  private static class SingleNode
  {
    final Set <SingleNode> followingNodes = new HashSet <SingleNode> ();
    final Name name;
    final int index;
    boolean repeated = false;

    SingleNode (final Name name, final int index)
    {
      this.name = name;
      this.index = index;
    }
  }

  private static class ParticleNode
  {
    final int index;
    Particle particle;
    int refCount = 0;
    Set <ParticleNode> followingNodes = new HashSet <ParticleNode> ();

    ParticleNode (final int index)
    {
      this.index = index;
    }

    void addFollowing (final ParticleNode p)
    {
      if (p != this)
      {
        if (!followingNodes.contains (p))
        {
          p.refCount++;
          followingNodes.add (p);
        }
      }
    }
  }

  private static class StronglyConnectedComponentsFinder
  {
    private final int [] visited;
    private final SingleNode [] root;
    private int visitIndex = 0;
    private final Stack <SingleNode> stack = new Stack <SingleNode> ();
    private final ParticleNode [] particleNodes;
    private final SingleNode [] singleNodes;
    private int nParticles = 0;

    StronglyConnectedComponentsFinder (final int nNodes)
    {
      visited = new int [nNodes];
      root = new SingleNode [nNodes];
      particleNodes = new ParticleNode [nNodes];
      singleNodes = new SingleNode [nNodes];
    }

    ParticleNode makeDag (final SingleNode start)
    {
      visit (start);
      for (int i = 0; i < singleNodes.length; i++)
        for (final SingleNode node : singleNodes[i].followingNodes)
          particleNodes[i].addFollowing (particleNodes[node.index]);
      return particleNodes[start.index];
    }

    /**
     * http://citeseer.nj.nec.com/nuutila94finding.html
     */
    void visit (final SingleNode v)
    {
      root[v.index] = v;
      visited[v.index] = ++visitIndex;
      singleNodes[v.index] = v;
      stack.push (v);
      for (final SingleNode w : v.followingNodes)
      {
        if (visited[w.index] == 0)
          visit (w);
        if (particleNodes[w.index] == null)
          root[v.index] = firstVisited (root[v.index], root[w.index]);
      }
      if (root[v.index] == v)
      {
        SingleNode w = stack.pop ();
        final ParticleNode pn = new ParticleNode (nParticles++);
        pn.particle = makeParticle (w.name);
        particleNodes[w.index] = pn;
        if (w != v)
        {
          do
          {
            w = stack.pop ();
            particleNodes[w.index] = pn;
            pn.particle = new ChoiceParticle (makeParticle (w.name), pn.particle);
          } while (w != v);
          pn.particle = new OneOrMoreParticle (pn.particle);
        }
        else
        {
          if (w.repeated)
            pn.particle = new OneOrMoreParticle (pn.particle);
        }
      }
    }

    SingleNode firstVisited (final SingleNode n1, final SingleNode n2)
    {
      return visited[n1.index] < visited[n2.index] ? n1 : n2;
    }

  }

  private static class ParticleBuilder
  {
    private final int [] rank;
    private int currentRank = 0;
    private Particle rankParticleChoice;
    private Particle followParticle;
    /**
     * Sum of the refCounts of the nodes in the ranks (not necessarily
     * immediately) following the current rank.
     */
    int followRanksTotalRefCount = 0;
    /**
     * Sum of the refCounts of the nodes in the current rank.
     */
    int currentRankTotalRefCount = 0;

    /**
     * Number of references that are from nodes in the current or following
     * ranks.
     */
    int totalCoveredRefCount = 0;

    ParticleBuilder (final int nNodes)
    {
      rank = new int [nNodes];
    }

    Particle build (final ParticleNode start)
    {
      visit (start);
      if (followParticle == null)
        followParticle = new EmptyParticle ();
      return followParticle;
    }

    void visit (final ParticleNode node)
    {
      int maxRank = 0;
      for (final ParticleNode follow : node.followingNodes)
      {
        if (rank[follow.index] == 0)
          visit (follow);
        maxRank = Math.max (maxRank, rank[follow.index]);
      }
      final int nodeRank = maxRank + 1;
      rank[node.index] = nodeRank;
      if (nodeRank == currentRank)
      {
        rankParticleChoice = new ChoiceParticle (rankParticleChoice, node.particle);
        currentRankTotalRefCount += node.refCount;
      }
      else
      {
        if (totalCoveredRefCount != followRanksTotalRefCount)
          rankParticleChoice = new ChoiceParticle (rankParticleChoice, new EmptyParticle ());
        if (followParticle == null)
          followParticle = rankParticleChoice;
        else
          followParticle = new SequenceParticle (rankParticleChoice, followParticle);
        followRanksTotalRefCount += currentRankTotalRefCount;
        rankParticleChoice = node.particle;
        currentRankTotalRefCount = node.refCount;
        currentRank = nodeRank;
      }
      totalCoveredRefCount += node.followingNodes.size ();
    }
  }

  private static class ParticleMerger
  {
    private final boolean [] done;

    private ParticleMerger (final int nNodes)
    {
      this.done = new boolean [nNodes];
    }

    void merge (final ParticleNode node)
    {
      if (done[node.index])
        return;
      done[node.index] = true;
      if (node.particle != null)
      {
        while (node.followingNodes.size () == 1)
        {
          final ParticleNode follower = node.followingNodes.iterator ().next ();
          if (follower.refCount != 1 || follower.particle == null)
            break;
          node.particle = new SequenceParticle (node.particle, follower.particle);
          node.followingNodes = follower.followingNodes;
        }
      }
      for (final ParticleNode follower : node.followingNodes)
        merge (follower);
    }

  }

  ContentModelInferrerImpl ()
  {
    startNode = lookup (START);
    endNode = lookup (END);
    prevNode = startNode;
  }

  @Override
  public void addElement (final Name elementName)
  {
    final SingleNode node = lookup (elementName);
    if (node == prevNode)
      prevNode.repeated = true;
    else
    {
      prevNode.followingNodes.add (node);
      prevNode = node;
    }
  }

  @Override
  public void endSequence ()
  {
    prevNode.followingNodes.add (endNode);
    prevNode = startNode;
  }

  private SingleNode lookup (final Name name)
  {
    SingleNode node = nameMap.get (name);
    if (node == null)
    {
      node = new SingleNode (name, nameMap.size ());
      nameMap.put (name, node);
    }
    return node;
  }

  @Override
  public Particle inferContentModel ()
  {
    if (startNode.followingNodes.size () == 0 || prevNode != startNode)
      throw new IllegalStateException ();
    final ParticleNode start = new StronglyConnectedComponentsFinder (nameMap.size ()).makeDag (lookup (START));
    final int nNodes = start.index + 1;
    new ParticleMerger (nNodes).merge (start);
    return new ParticleBuilder (nNodes).build (start);
  }

  private static Particle makeParticle (final Name name)
  {
    if (name == START || name == END)
      return null;
    return new ElementParticle (name);
  }

  @Override
  public Set <Name> getElementNames ()
  {
    final Set <Name> elementNames = new HashSet <Name> ();
    elementNames.addAll (nameMap.keySet ());
    elementNames.remove (START);
    elementNames.remove (END);
    return elementNames;
  }

}
