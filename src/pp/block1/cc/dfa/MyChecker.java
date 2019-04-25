package pp.block1.cc.dfa;


public class MyChecker implements Checker {

    @Override
    public boolean accepts(State start, String word) {
        State currentState = start;
        for (int i=0; i < word.length(); i++) {
            char character = word.charAt(i);
            if(!currentState.hasNext(character)) {
                return false;
            }
            currentState = currentState.getNext(character);
        }
        return currentState.isAccepting();
    }
}