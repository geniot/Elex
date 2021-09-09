package io.github.geniot.elex.tools.convert;


import java.util.*;

import static io.github.geniot.elex.tools.convert.DslUtils.*;


public class DslLine {

    String wellFormed;

    String origLine;

    public DslLine(String input) {
        this.origLine = input;

        input = input.replaceAll("\\[p\\] \\[\\/p\\]", " ");
        String[] tokens = tokenize(input);

        if (isWellFormed(tokens)) {
            this.wellFormed = input;
            return;
        } else {
//            moveSpaces(tokens);

            String lastToken = tokens[tokens.length - 1];
            String firstToken = tokens[0];
            if (isTag(lastToken) &&
                    isClosing(lastToken) &&
                    new Tag(lastToken).name.equals("m") &&
                    (!isTag(firstToken) || !new Tag(firstToken).name.equals("m"))
            ) {
                //sometimes it's just necessary to add m1 opening tag
                List<String> tokensList = new ArrayList<>(Arrays.asList(tokens));
                tokensList.add(0, "[m1]");
                String[] newTokenArr = tokensList.toArray(new String[tokensList.size()]);
                if (isWellFormed(newTokenArr)) {
                    this.wellFormed = glue(newTokenArr);
                    return;
                }
            }
        }

        SortedSet<Tag> openedTags = new TreeSet<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (isTag(token)) {
                if (isOpening(token)) {
                    openedTags.add(new Tag(token));
                } else {
                    Tag tagToRemove = findTagByName(openedTags, tagName(token));
                    if (tagToRemove != null) {
                        openedTags.remove(tagToRemove);
                    } else {//we found a closing tag with no matching opening tag
                        //marking all current text elements with this tag
                        for (TextElement tElement : textElementList) {
                            tElement.addTag(new Tag(token));
                        }
                    }
                }
            } else {
                //collecting closing tags
                SortedSet<Tag> closedTags = new TreeSet<>();
                for (int k = i + 1; k < tokens.length; k++) {
                    String possibleClosingTag = tokens[k];
                    if (isTag(possibleClosingTag) && isClosing(possibleClosingTag)) {
                        closedTags.add(new Tag(possibleClosingTag));
                    } else {
                        break;
                    }
                }
                TextElement textElement = new TextElement(token);

//                if (token.contains("(red-billed)")) {
//                    System.out.println(token);
//                }

                TreeSet<Tag> clonedOpenedTags = cloneSet(openedTags);
                for (Tag openedTag : clonedOpenedTags) {
                    if (closedTags.contains(openedTag)) {
                        openedTag.weight += 10;
                    }
                }
                textElement.setTags(clonedOpenedTags);
                closedTags.clear();
                textElementList.add(textElement);
            }
        }
    }

    private void moveSpaces(String[] tokens) {
        //if there is a space between closing tags let's move it out
        //[/i][/c] [/p][b] should become [/i][/c][/p] [b]
        for (int i = 0; i < tokens.length - 1; i++) {
            if (isClosing(tokens[i]) &&
                    (tokens[i + 1].trim().equals("") || tokens[i + 1].trim().equals(" ")) &&
                    isClosing(tokens[i + 2])
            ) {
                String tmp = tokens[i + 1];
                tokens[i + 1] = tokens[i + 2];
                tokens[i + 2] = tmp;
            }
        }
    }

    private TreeSet<Tag> cloneSet(SortedSet<Tag> orig) {
        TreeSet<Tag> res = new TreeSet<>();
        for (Tag tag : orig) {
            res.add(tag.copy());
        }
        return res;
    }

    public void debug() {
        for (TextElement textElement : textElementList) {
            textElement.debug();
        }
    }

    private Tag findTagByName(SortedSet<Tag> openedTags, String tagName) {
        for (Tag t : openedTags) {
            if (t.name.equals(tagName)) {
                return t;
            }
        }
        return null;
    }

    List<TextElement> textElementList = new ArrayList<>();


    @Override
    public String toString() {
        if (wellFormed != null) {
            return wellFormed;
        }

        StringBuffer stringBuffer = new StringBuffer();
        TreeSet<Tag> openTags = new TreeSet<>();
        for (TextElement textElement : textElementList) {

            Iterator<Tag> openTagsIterator = openTags.descendingIterator();
            while (openTagsIterator.hasNext()) {
                Tag tag = openTagsIterator.next();
                if (tag.name.equals("p") || //we should always close p as soon as possible
                        !tagNames(textElement.tags).contains(tag.name)) {
                    openTagsIterator.remove();
                    stringBuffer.append(tag.close());
                }
            }

            for (Tag tag : textElement.tags) {
                if (!tagNames(openTags).contains(tag.name)) {
                    openTags.add(tag);
                    stringBuffer.append(tag.open());
                }
            }
//            if (textElement.text.contains("Student's")) {
//                System.out.println("stop");
//            }
            stringBuffer.append(textElement.text);
        }
        for (Tag tag : openTags.descendingSet()) {
            stringBuffer.append(tag.close());
        }
        String line = stringBuffer.toString();
        if (!isWellFormed(tokenize(line))) {
            throw new RuntimeException("Not well-formed: " + origLine);
        }
        return line;
    }

    public Set<String> tagNames(SortedSet<Tag> tags) {
        Set<String> set = new HashSet<>();
        for (Tag t : tags) {
            set.add(t.name);
        }
        return set;
    }
}

