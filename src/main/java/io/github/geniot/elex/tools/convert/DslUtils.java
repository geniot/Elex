package io.github.geniot.elex.tools.convert;

import java.util.Stack;

public class DslUtils {
    public static String noEscape = "(?<!\\\\)";
    public static String validBracketO = noEscape + "\\[";
    public static String validBracketC = noEscape + "\\]";
    public static String anythingButBracket = "[^\\[]+";
    public static String anythingButBracket50 = "[^\\[]{1,50}";
    public static String anyTag = "(" + validBracketO + anythingButBracket50 + validBracketC + "|\r\n)";

    public static String tagName(String tag) {
        if (!isTag(tag)) {
            throw new RuntimeException("Not a tag: " + tag);
        }
        tag = tag.replaceAll("\\[|\\]", "");
        tag = tag.replaceAll("/", "");
        if (tag.contains(" ")) {
            tag = tag.split(" ")[0];
        }
        return tag;
    }

    public static String tagNameShort(String tag) {
        return tagName(tag).replaceAll("[0-9]+", "");
    }

    public static boolean isTag(String token) {
        return token.matches("^" + noEscape + "\\[" + anythingButBracket + "\\]" + noEscape + "$");
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
        return entryStr.split("((?<=" + anyTag + ")|(?=" + anyTag + "))");
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

