package pp.block2.cc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;
import pp.block2.cc.ll.*;

public class LLCalcTest {
	Grammar sentenceG = Grammars.makeSentence();
	// Define the non-terminals
	NonTerm subj = sentenceG.getNonterminal("Subject");
	NonTerm obj = sentenceG.getNonterminal("Object");
	NonTerm sent = sentenceG.getNonterminal("Sentence");
	NonTerm mod = sentenceG.getNonterminal("Modifier");
	// Define the terminals
	Term adj = sentenceG.getTerminal(Sentence.ADJECTIVE);
	Term noun = sentenceG.getTerminal(Sentence.NOUN);
	Term verb = sentenceG.getTerminal(Sentence.VERB);
	Term end = sentenceG.getTerminal(Sentence.ENDMARK);
	// Now add the last rule, causing the grammar to fail
	Grammar sentenceXG = Grammars.makeSentence();
	{    sentenceXG.addRule(mod, mod, mod);
	}
	LLCalc sentenceXLL = createCalc(sentenceXG);

	Grammar ifG = Grammars.makeIf(); // to be defined (Ex. 2-CC.4.1)
	// Define the non-terminals
	 NonTerm stat = ifG.getNonterminal("Stat");
	 NonTerm elsePart = ifG.getNonterminal("ElsePart");
	// Define the terminals (take from the right lexer grammar!)
	 Term ifT = ifG.getTerminal(If.IF);
	 Term thenT = ifG.getTerminal(If.THEN);
	 Term assignT = ifG.getTerminal(If.ASSIGN);
	 Term elseT = ifG.getTerminal(If.ELSE);
	 Term condT = ifG.getTerminal(If.COND);
	 Term eof = Symbol.EOF;
	 Term empty = Symbol.EMPTY;
	 LLCalc ifLL = createCalc(ifG);

	Grammar LRQG = Grammars.makeLRQ(); // to be defined (Ex. 2-CC.4.1)
	NonTerm L = LRQG.getNonterminal("L");
	NonTerm R = LRQG.getNonterminal("R");
	NonTerm Q = LRQG.getNonterminal("Q");
	Term a = LRQG.getTerminal(LRQ.A);
	Term b = LRQG.getTerminal(LRQ.B);
	Term c = LRQG.getTerminal(LRQ.C);
	LLCalc LRQLL = createCalc(LRQG);








	/** Tests the LL-calculator for the Sentence grammar. */
	@Test
	public void testSentenceOrigLL1() {
		// Without the last (recursive) rule, the grammar is LL-1
		assertTrue(createCalc(sentenceG).isLL1());
	}

	@Test
	public void testSentenceXFirst() {
		Map<Symbol, Set<Term>> first = sentenceXLL.getFirst();
		assertEquals(set(adj, noun), first.get(sent));
		assertEquals(set(adj, noun), first.get(subj));
		assertEquals(set(adj, noun), first.get(obj));
		assertEquals(set(adj), first.get(mod));
	}
	
	@Test
	public void testSentenceXFollow() {
		// FOLLOW sets
		Map<NonTerm, Set<Term>> follow = sentenceXLL.getFollow();
		assertEquals(set(Symbol.EOF), follow.get(sent));
		assertEquals(set(verb), follow.get(subj));
		assertEquals(set(end), follow.get(obj));
		assertEquals(set(noun, adj), follow.get(mod));
	}
	
	@Test
	public void testSentenceXFirstPlus() {
		// Test per rule
		Map<Rule, Set<Term>> firstp = sentenceXLL.getFirstp();
		List<Rule> subjRules = sentenceXG.getRules(subj);
		assertEquals(set(noun), firstp.get(subjRules.get(0)));
		assertEquals(set(adj), firstp.get(subjRules.get(1)));
	}
	
	@Test
	public void testSentenceXLL1() {
		assertFalse(sentenceXLL.isLL1());
	}

	@Test
	public void testIfFirst() {
		Map<Symbol, Set<Term>> first = ifLL.getFirst();
		assertEquals(set(assignT, ifT), first.get(stat)); // (insert other tests) }
		assertEquals(set(elseT, Symbol.EMPTY), first.get(elsePart));
		assertEquals(set(ifT), first.get(ifT));
		assertEquals(set(condT), first.get(condT));
		assertEquals(set(thenT), first.get(thenT));
		assertEquals(set(elseT), first.get(elseT));
		assertEquals(set(empty), first.get(empty));
	}

	@Test
	public void testIfFollow() {
		Map<NonTerm, Set<Term>> follow = ifLL.getFollow();
		assertEquals(set(eof, elseT), follow.get(stat));
		assertEquals(set(eof, elseT), follow.get(elsePart));
	}

	@Test
	public void testIfFirstPlus() {
		Map<Rule, Set<Term>> firstp = ifLL.getFirstp();
		List<Rule> elseRules = ifG.getRules(elsePart);
		assertEquals(set(elseT), firstp.get(elseRules.get(0)));
		assertEquals(set(elseT, eof, empty ), firstp.get(elseRules.get(1))); //TODO: added it in the cc-drive file as well
		assertEquals(set(assignT), firstp.get(ifG.getRules(stat).get(0)));
		assertEquals(set(ifT), firstp.get(ifG.getRules(stat).get(1)));
	}

	@Test
	public void testIfLL1() {
		assertFalse(ifLL.isLL1()); }

	@Test
	public void testLRQFirst() {
		Map<Symbol, Set<Term>> first = LRQLL.getFirst();
		assertEquals(set(a,c,b), first.get(L));
		assertEquals(set(a,c), first.get(R));
		assertEquals(set(b), first.get(Q));
		assertEquals(set(a), first.get(a));
		assertEquals(set(b), first.get(b));
		assertEquals(set(c), first.get(c));
	}

	@Test
	public void testLRQFollow() {
		Map<NonTerm, Set<Term>> follow = LRQLL.getFollow();
		assertEquals(set(eof), follow.get(L));
		assertEquals(set(a,b), follow.get(R));
		assertEquals(set(b), follow.get(Q));
	}

	@Test
	public void testLRQFirstPlus() {
		Map<Rule, Set<Term>> firstp = LRQLL.getFirstp();
		assertEquals(set(a,c), firstp.get(LRQG.getRules(L).get(0)));
		assertEquals(set(b), firstp.get(LRQG.getRules(L).get(1)));
		assertEquals(set(a), firstp.get(LRQG.getRules(R).get(0)));
		assertEquals(set(c), firstp.get(LRQG.getRules(R).get(1)));
		assertEquals(set(a,c), firstp.get(LRQG.getRules(R).get(2)));
		assertEquals(set(b), firstp.get(LRQG.getRules(Q).get(0)));
		assertEquals(set(b), firstp.get(LRQG.getRules(Q).get(1)));
	}

	@Test
	public void testLRQLL1() {
		Grammar lrqll1 = Grammars.makeLRQLL1();
//		LLCalc test = createCalc(lrqll1);
		LLCalc test = new MyLLCalc(sentenceG);
		Map<Rule, Set<Term>> firstplusmap = test.getFirstp();
		for (Rule rule : sentenceG.getRules()) {
			System.out.println(rule.toString() + ":" + firstplusmap.get(rule));
		}
		assertFalse(LRQLL.isLL1()); }

	/** Creates an LL1-calculator for a given grammar. */
	private LLCalc createCalc(Grammar g) {
		return new MyLLCalc(g);
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> set(T... elements) {
		return new HashSet<>(Arrays.asList(elements));
	}
}
