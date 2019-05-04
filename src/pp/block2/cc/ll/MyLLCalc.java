package pp.block2.cc.ll;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;
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
        for (NonTerm nonTerm : this.grammar.getNonterminals()) {
            follow.put(nonTerm, new HashSet<>());
        }
        Set<Term> aaa = new HashSet<Term>();
        aaa.add((Term) Symbol.EOF);
        follow.put(this.grammar.getStart(), aaa);
        boolean hasChanged = true;
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
                        if (followLE.addAll(followLHS)) {
                            hasChanged = true;
                            follow.put(lhs, followLE);
                        }
                    }
                    for (int i = rhs.size() - 2; i >= 0; i--) {
                        Symbol currentElement = rhs.get(i);
                        if (currentElement instanceof NonTerm) {
                            Set<Term> first = firstMap.get(rhs.get(i+1));
                            Set<Term> followCE = follow.get(currentElement);
                            if (followCE.addAll(first)) {
                                hasChanged = true;
                                follow.put((NonTerm) currentElement, followCE);
                            }
                            int t = i;
                            while (t <= rhs.size() -3  && first.contains(Symbol.EMPTY)) { // first has index i+1
                                if (first.contains(Symbol.EMPTY) && rhs.get(i+2) instanceof Term) {
                                    if (followCE.add((Term) rhs.get(i+2))) {
                                        hasChanged = true;
                                        follow.put((NonTerm) currentElement, followCE);
                                    }
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
        Map<Symbol, Set<Term>> firstMap = getFirst();
        Map<NonTerm,Set<Term>> followMap = getFollow();
        Map<Rule, Set<Term>> ret = new HashMap<>();
        for (Rule rule : this.grammar.getRules()) {
            Set<Term> ruleres = firstMap.get(rule.getRHS().get(0));
            if (ruleres.contains(Symbol.EMPTY)) {
                ruleres.addAll(followMap.get(rule.getLHS()));
            }
            ret.put(rule, ruleres);
        }
        return ret;
    }

    /**
     * Indicates if the grammar of this calculator instance is LL(1).
     */
    @Override
    public boolean isLL1() {
        Map<Rule, Set<Term>> firstPlus = getFirstp();
        for (NonTerm lhs : grammar.getNonterminals()) {
            int last = grammar.getRules(lhs).size() - 1;
            int rule1index = 0;
            List<Rule> rules = grammar.getRules(lhs);
            while (rule1index < last) {
                int rule2index = rule1index + 1;
                while (rule2index <= last) {
                    if (firstPlus.get(rules.get(rule1index)).containsAll(firstPlus.get(rules.get(rule2index)))) {
                        return false;
                    }
                    rule2index += 1;
                }
                rule1index += 1;
            }
        }
        return true;
    }
}
