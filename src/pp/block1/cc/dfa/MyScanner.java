package pp.block1.cc.dfa;

import java.util.ArrayList;
import java.util.List;

public class MyScanner implements Scanner {

    @Override
    public List<String> scan(State dfa, String text) {
        List<String> stringList = new ArrayList<>();
        String currentFound = "";
        String currentString = "";
        State currentState = dfa;
        if (text.length() == 0) {
            if (dfa.isAccepting()) {
                stringList.add("");
            }
        }
        for (int i=0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (!currentState.hasNext(character)) {
                if (currentFound.equals("")) {
                    return stringList;
                } else {
                    stringList.add(currentFound);
                    currentFound = "";
                    currentString = "";
                    currentState = dfa;
                    i--;
                }
            } else {
                currentState = currentState.getNext(character);
                currentString += character;
                if (currentState.isAccepting()) {
                    currentFound = currentString;
                }
            }
        }
        if (!currentFound.equals("")) {
            stringList.add(currentFound);
        }
        return stringList;
    }
}
