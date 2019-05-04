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
        return null;
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
        return false;
    }
}
