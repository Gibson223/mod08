/**
 * 
 */
package pp.block2.cc.ll;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.SymbolFactory;
import pp.block2.cc.Term;

/**
 * Class containing some example grammars.
 * @author Arend Rensink
 *
 */
public class Grammars {
	/** Returns a grammar for simple English sentences. */
	public static Grammar makeSentence() {
		// Define the non-terminals
		NonTerm sent = new NonTerm("Sentence");
		NonTerm subj = new NonTerm("Subject");
		NonTerm obj = new NonTerm("Object");
		NonTerm mod = new NonTerm("Modifier");
		// Define the terminals, using the Sentence.g4 lexer grammar
		// Make sure you take the token constantss from the right class!
		SymbolFactory fact = new SymbolFactory(Sentence.class);
		Term noun = fact.getTerminal(Sentence.NOUN);
		Term verb = fact.getTerminal(Sentence.VERB);
		Term adj = fact.getTerminal(Sentence.ADJECTIVE);
		Term end = fact.getTerminal(Sentence.ENDMARK);
		// Build the context free grammar
		Grammar g = new Grammar(sent);
		g.addRule(sent, subj, verb, obj, end);
		g.addRule(subj, noun);
		g.addRule(subj, mod, subj);
		g.addRule(obj, noun);
		g.addRule(obj, mod, obj);
		g.addRule(mod, adj);
		return g;
	}
	public static Grammar makeIf() {
		// Define the non-terminals
		NonTerm stat = new NonTerm("Stat");
		NonTerm elsePart = new NonTerm("ElsePart");
		// Define the terminals, using the Sentence.g4 lexer grammar
		// Make sure you take the token constantss from the right class!
		SymbolFactory fact = new SymbolFactory(If.class);
		Term ifT = fact.getTerminal(If.IF);
		Term thenT = fact.getTerminal(If.THEN);
		Term assignT = fact.getTerminal(If.ASSIGN);
		Term elseT = fact.getTerminal(If.ELSE);
		Term condT = fact.getTerminal(If.COND);
		// Build the context free grammar
		Grammar g = new Grammar(stat);
		g.addRule(stat, assignT);
		g.addRule(stat, ifT, condT, thenT, stat, elsePart);
		g.addRule(elsePart, elseT, stat);
		g.addRule(elsePart, Symbol.EMPTY );
		return g;
	}
	public static Grammar makeLRQ() {
		// Define the non-terminals
		NonTerm L = new NonTerm("L");
		NonTerm R = new NonTerm("R");
		NonTerm Q = new NonTerm("Q");
		// Define the terminals, using the Sentence.g4 lexer grammar
		// Make sure you take the token constantss from the right class!
		SymbolFactory fact = new SymbolFactory(LRQ.class);
		Term a = fact.getTerminal(LRQ.A);
		Term b = fact.getTerminal(LRQ.B);
		Term c = fact.getTerminal(LRQ.C);
		// Build the context free grammar
		Grammar g = new Grammar(L);
		g.addRule(L, R, a);
		g.addRule(L, Q, b, a);
		g.addRule(R, a,b,a);
		g.addRule(R, c,a,b,a);
		g.addRule(R, R, b,c);
		g.addRule(Q, b,b,c);
		g.addRule(Q, b,c);
		return g;
	}
}
