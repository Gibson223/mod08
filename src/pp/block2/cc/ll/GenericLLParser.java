package pp.block2.cc.ll;

import java.util.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import pp.block2.cc.AST;
import pp.block2.cc.NonTerm;
import pp.block2.cc.ParseException;
import pp.block2.cc.Parser;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;

/** Generic table-driven LL(1)-parser. */
public class GenericLLParser implements Parser {
	/** The grammar underlying this parser instance. */
	private final Grammar g;
	/** The LL-calculater for the grammar. */
	private final LLCalc calc;
	/** Map from non-terminals to maps of rules indexed by terminal. */
	private Map<NonTerm, Map<Term, Rule>> ll1Table;
	/** Current index in the token list. */
	private int index;
	/** Token list of the currently parsed input. */
	private List<? extends Token> tokens;


	public GenericLLParser(Grammar g) {
		this.g = g;
		this.calc = new MyLLCalc(g); // TODO Instantiate your LLCalc-implementation
	}

	@Override
	public AST parse(Lexer lexer) throws ParseException {
		this.tokens = lexer.getAllTokens();
		this.index = 0;
		return parse(this.g.getStart());
	}
	
	/** Parses the start of the token stream according to a given
	 * symbol. If it is a terminal, that should be the first token;
	 * otherwise, it is a non-terminal that should be expanded 
	 * according to the next token in the token stream, using the
	 * FIRST+-lookup table and recursively calling {@link #parse(Rule)}
	 * @param symb the symbol according to which the token stream 
	 * should be parsed
	 * @return the sub-AST resulting from the parsing of symb;
	 * or null if the symbol expands to the empty string
	 * @throws ParseException if the symbol cannot be parsed
	 * because the token stream does not contain the expected symbols
	 */
	private AST parse(Symbol symb) throws ParseException {// TODO fill in
		AST ret;
		if (symb instanceof Term) {
			ret = new AST((Term)symb, this.next());
		} else {
			ret = this.parse(this.lookup((NonTerm) symb));
		}
//		System.out.println("should never get here.grammar no start...??");
		return ret;
	}

	/** Parses the start of the token stream according to a given
	 * rule, recursively calling {@link #parse(Symbol)} to process
	 * the RHS.
	 * @return the sub-AST resulting from the parsing of the rule.
	 * The top node is the node for the LHS of the rule, its direct
	 * children correspond to the symbols of the rule's RHS.
	 * @throws ParseException if the symbol cannot be parsed
	 * because the token stream does not contain the expected symbols
	 */
	private AST parse(Rule rule) throws ParseException {// TODO fill in
		AST ret = new AST(rule.getLHS());
		for (Symbol sim : rule.getRHS()) {
//		for (Symbol sim : this.lookup((NonTerm) rule.getLHS()).getRHS()) {
			if (sim instanceof Term) {
				ret.addChild(new AST((Term) sim, this.next()));
//				ret.addChild(parse(sim));
			} else {
				ret.addChild(parse((NonTerm) sim));
			}
		}
		return ret;
	}

	/** Uses the lookup table to look up the rule to which
	 * a given nonterminal should be expanded.
	 * The next rule is determined by the next token, using the
	 * FIRST+-set of the nonterminal.
	 * @throws ParseException if the lookup table does not 
	 * contain a rule for the nonterminal in combination with
	 * the first token in the token stream.
	 */
	private Rule lookup(NonTerm nt) throws ParseException {
		Rule result;
		if (atEnd()) {
			result = getLL1Table().get(nt).get(Term.EOF);
			if (result == null) {
				throw new ParseException("Reading beyond end of input");
			}
		} else {
			Token nextToken = peek();
			Term term = this.g.getTerminal(nextToken.getType());
			result = getLL1Table().get(nt).get(term);
			if (result == null) {
				throw new ParseException(String.format(
						"Line %d:%d - no rule for '%s' on token '%s'",
						nextToken.getLine(), 
						nextToken.getCharPositionInLine(),
						nt.getName(), nextToken));
			}
		}
		return result;
	}

	/** Tests whether the end of input has been reached. */
	private boolean atEnd() {
		return this.index >= this.tokens.size();
	}

	/** Returns the next token, without moving the token index. */
	private Token peek() throws ParseException {
		if (this.index >= this.tokens.size()) {
			throw new ParseException("Reading beyond end of input");
		}
		return this.tokens.get(this.index);
	}

	/** Returns the next token and moves up the token index. */
	private Token next() throws ParseException {
		Token result = peek();
		this.index++;
		return result;
	}

	/** Lazily builds and then returns the lookup table. */
	private Map<NonTerm, Map<Term, Rule>> getLL1Table() {
		if (this.ll1Table == null) {
			this.ll1Table = calcLL1Table();
		}
		return this.ll1Table;
	}

	/** Constructs the {@link #ll1Table}. */
	private Map<NonTerm, Map<Term, Rule>> calcLL1Table() { // TODO fill in
		Map<Rule, Set<Term>>firstplusmap = this.calc.getFirstp();
		Map<NonTerm, Map<Term, Rule>> ret = new HashMap<>();
		for (NonTerm A : this.g.getNonterminals()) {
			for (Term w : this.g.getTerminals()) {
				Map<Term, Rule> wmap = new HashMap<Term, Rule>();
				wmap.put(w, null );
				ret.put(A, wmap);
			}
			for (Rule rule : this.g.getRules(A)) {
				Set<Term>  terms = firstplusmap.get(rule);
				for (Term term : terms) { // Symbol.EOF should be included, so no extra if
					if (!(ret.get(A).get(term) == null)) {
						System.out.println("should never be printed!!!");
						System.out.println("it is expected that there is only one rule, otherwise grammar is not ll1");
					}
					ret.get(A).put(term, rule);
				}
			}
		}

		return ret;
	}
}
