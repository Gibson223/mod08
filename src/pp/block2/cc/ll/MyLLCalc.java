package pp.block2.cc.ll;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;

<<<<<<< HEAD
=======
import javax.sound.midi.Sequencer;
>>>>>>> origin/master
import java.util.*;

public class MyLLCalc implements LLCalc {
    private Grammar grammar;

    /**
     * Returns the FIRST-map for the grammar of this calculator instance.
     */
    public MyLLCalc(Grammar grammar) {
        this.grammar = grammar;
    }

    @Override
    public Map<Symbol, Set<Term>> getFirst() {
        Map<Symbol, Set<Term>> ret = new HashMap<Symbol, Set<Term>>();
//      add all the first of the terminals
        for (Term term : grammar.getTerminals()){
            Set<Term> empty = new HashSet<Term>();
            empty.add(term);
            ret.put((Symbol) term, empty);
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (NonTerm lhs : grammar.getNonterminals()) {
                Set<Term> was = ret.getOrDefault(lhs, new HashSet<Term>());
                int i = 0;
                int lenRHS = 10;
                for (Rule rule : grammar.getRules(lhs)) {
                    List<Symbol> rhs = rule.getRHS();
                    lenRHS = rule.getRHS().size();
                    Set<Term> firstb = ret.getOrDefault(rhs.get(0), new HashSet<Term>());
                    Set<Term> firstbwithoutepsilon = new HashSet<Term>(firstb);
                    boolean prevcontained = firstbwithoutepsilon.remove(Symbol.EMPTY);
                    if (was.addAll(firstbwithoutepsilon)) {
                        changed = true;
                    }
                    i = 1;
                    while (i < lenRHS && prevcontained) {
                        firstb = ret.getOrDefault(rhs.get(i), new HashSet<Term>());
                        firstbwithoutepsilon = new HashSet<Term>(firstb);
                        prevcontained =firstbwithoutepsilon.remove(Symbol.EMPTY);
                        if (was.addAll(firstbwithoutepsilon)) {
                            changed = true;
                        }
                        i = i + 1;
                    }
                }
                if (i == lenRHS) {
                    was.add(Symbol.EMPTY);
                }
                ret.put(lhs, was);
            }
        }
    return ret;
    }

    /**
     * Returns the FOLLOW-map for the grammar of this calculator instance.
     */
    @Override
    public Map<NonTerm, Set<Term>> getFollow() {
        Map<NonTerm, Set<Term>> follow = new HashMap<>();
        boolean hasChanged = true;
        for (NonTerm nonTerm : this.grammar.getNonterminals()) {
            follow.put(nonTerm, new HashSet<>());
        }
        follow.put(this.grammar.getStart(), Collections.singleton(Symbol.EOF));
        Map<Symbol, Set<Term>> firstMap = getFirst();
        while (hasChanged) {
            hasChanged = false;
            for (NonTerm nonTerm : follow.keySet()) {
                for (Rule rule : grammar.getRules(nonTerm)) {
                    NonTerm lhs = rule.getLHS();
                    List<Symbol> rhs = rule.getRHS();
                    Symbol lastElement = rhs.get(rhs.size() - 1);
                    if (lastElement instanceof NonTerm) {
                        Set<Term> followLHS = follow.get(lhs);
                        Set<Term> followLE = follow.get(lastElement);
                        followLE.addAll(followLHS);
                        follow.put(lhs, followLE);
                        hasChanged = true;
                    }
                    for (int i = rhs.size() - 2; i >= 0; i--) {
                        Symbol currentElement = rhs.get(i);
                        if (currentElement instanceof NonTerm) {
                            Set<Term> first = firstMap.get(rhs.get(i+1));
                            Set<Term> followCE = follow.get(currentElement);
                            followCE.addAll(first);
                            follow.put(lhs, followCE);
                            hasChanged = true;
                        } else {
                            if ((rhs.get(i-1) instanceof NonTerm) && (rhs.get(i-2) instanceof  NonTerm)) {
                                Set<Term> oneBeforeCurrent = firstMap.get(rhs.get((i-1)));
                                Set<Term> twoBeforeCurrent = firstMap.get(rhs.get(i-2));
                                if (oneBeforeCurrent.contains(Symbol.EMPTY)) {
                                    twoBeforeCurrent.add((Term) currentElement);
                                    follow.put((NonTerm) rhs.get(i-2), twoBeforeCurrent);
                                    hasChanged = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (NonTerm nt : follow.keySet()) {
            Set<Term> termSet = follow.get(nt);
            termSet.remove(Symbol.EMPTY);
            follow.put(nt, termSet);
        }
        return follow;
    }

    /**
     * Returns the FIRST+-map for the grammar of this calculator instance.
     */
    @Override
    public Map<Rule, Set<Term>> getFirstp() {
        return null;
    }

    /**
     * Indicates if the grammar of this calculator instance is LL(1).
     */
    @Override
    public boolean isLL1() {
        Map<Rule, Set<Term>> firstPlus = getFirstp();
        for (Rule rule1 : firstPlus.keySet()) {
            for (Rule rule2 : firstPlus.keySet()) {
                if ((rule1.getLHS()).equals(rule2.getLHS()) && !rule1.equals(rule2)) {
                    List<Symbol> rule2rhs = rule2.getRHS();
                    for (Symbol sym : rule1.getRHS()) {
                        if (rule2rhs.contains(sym)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
