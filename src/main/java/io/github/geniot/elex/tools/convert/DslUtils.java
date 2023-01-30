package io.github.geniot.elex.tools.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DslUtils {
    public static String noEscape = "(?<!\\\\)";
    public static String validBracketO = noEscape + "\\[";
    public static String validBracketC = noEscape + "\\]";
    public static String anythingButBracket = "[^\\[]+";
    public static String anythingButBracket50 = "[^\\[]{1,100}";
    public static String anyTag = "(" + validBracketO + anythingButBracket50 + validBracketC + "|\r\n)";

    public static String tagName(String tag) {
        if (!isTag(tag)) {
            throw new RuntimeException("Not a tag: " + tag);
        }
        StringBuilder stringBuilder = new StringBuilder();
        int from = 1;
        if (tag.charAt(from) == '/') {
            from = 2;
        }
        for (int i = from; i < tag.length() - 1; i++) {
            char c = tag.charAt(i);
            if (c != ' ') {
                stringBuilder.append(c);
            } else {
                return stringBuilder.toString();
            }
        }
        return stringBuilder.toString();
//        tag = tag.replaceAll("\\[|\\]", "");
//        tag = tag.replaceAll("/", "");
//        if (tag.contains(" ")) {
//            tag = tag.split(" ")[0];
//        }
//        return tag;
    }

    public static String tagNameShort(String tag) {
        return tagName(tag).replaceAll("[0-9]+", "");
    }

    public static boolean isTag(String token) {
        return token.charAt(0) == '[' && token.charAt(token.length() - 1) == ']';
//        return token.matches("^" + noEscape + "\\[" + anythingButBracket + "\\]" + noEscape + "$");
    }

    public static boolean isOpening(String tag) {
        return tag.charAt(1) != '/';
    }

    public static boolean isClosing(String tag) {
        if (!isTag(tag)) {
            return false;
        } else {
            return tag.charAt(1) == '/';
        }
    }

    public static String[] tokenize(String entryStr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < entryStr.length(); i++) {
            char c = entryStr.charAt(i);
            if (
                    i > 0 &&
                            (c == '[' || c == ']') &&
                            entryStr.charAt(i - 1) != '\\' &&
                            buffer.length() > 0
            ) {
                if (c == ']') {
                    buffer.append(c);
                }
                tokens.add(buffer.toString());
                buffer = new StringBuilder();
                if (c == '[') {
                    buffer.append(c);
                }
            } else {
                buffer.append(c);
            }
        }
        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
        }
        return tokens.toArray(new String[tokens.size()]);
//        return entryStr.split("((?<=" + anyTag + ")|(?=" + anyTag + "))");
    }

    public static String glue(String[] tokens) {
        return String.join("", tokens);
    }

    public static boolean isWellFormed(String[] tokens) {
        Stack<String> stack = new Stack<>();
        for (String token : tokens) {
            if (isTag(token)) {
                if (isOpening(token)) {
                    stack.push(token);
                } else {//is closing
                    if (stack.isEmpty()) {
                        return false;
                    }
                    if (tagNameShort(token).equals(tagNameShort(stack.peek()))) {
                        stack.pop();
                    } else {
//                        System.out.println("Not well-formed, closing tag wrong: " + token);
                        return false;
                    }
                }
            }
        }
        if (stack.size() != 0) {
//            System.out.println("Not well-formed, open tags left unclosed: " + stack);
            return false;
        }
        return true;
    }
}

