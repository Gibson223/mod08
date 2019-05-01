package pp.block1.cc.test;

import org.junit.Test;
import pp.block1.cc.antlr.Example;
import pp.block1.cc.antlr.Exercise12;

public class LexerTest {
	private static LexerTester tester = new LexerTester(Exercise12.class);

	@Test
	public void succeedingTest() {
		tester.correct("\"hey\"\"this\"");
		tester.correct("\"\"\"\"");
		tester.yields("\"this\"\"isa\"\"test\"", Exercise12.STRING);
		tester.wrong("\"test\"\"");
		tester.wrong("\"");
	}

	@Test
	public void spacesInKeywordTest() {
		// spaces in keywords are not in the rules
		tester.correct("\"whi \"\"  le do\"");
	}
}
