package pp.block1.cc.test;

import org.junit.Test;
import pp.block1.cc.antlr.Example;
import pp.block1.cc.antlr.Exercise13;

public class Exercise13Test {
	private static LexerTester tester = new LexerTester(Exercise13.class);

	@Test
	public void succeedingTest() {
		tester.correct("");
		tester.correct("La");
		tester.correct("Laaaaaaa");
		tester.correct("Laa   ");
		tester.correct("La La");
		tester.correct("Laaa   Laaaaa");
		tester.correct("La La La");
		tester.yields("La La La", Exercise13.LALA, Exercise13.LA);
		tester.correct("La LaLa Li");
		tester.wrong("LA");
		tester.wrong("La LaLa LI");
		tester.wrong("La La La Liiiiii");
		tester.wrong("L a ");
		tester.wrong("Li");
		tester.correct("La La LaLa");
		tester.correct("La La La La La Li");
		tester.yields("La Laaa La   Li", Exercise13.LALALALI);
		tester.yields("La La La La La Li", Exercise13.LALA, Exercise13.LALALALI);
	}
}
