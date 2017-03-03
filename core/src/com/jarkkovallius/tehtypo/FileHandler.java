package com.jarkkovallius.tehtypo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;



/**
 * FileHandler class is used to load and save files
 *
 * Created by Jarkko on 3.3.2017.
 */
public class FileHandler {

    /**
     * Loads words from XML file where words are inside of <s></s> tags
     *
     * @param filePath full path of xml file
     * @return Array of words or null if file not found
     */
    public Array<String> loadWordListFromXMLFile(String filePath) {
        FileHandle file = Gdx.files.internal(filePath);

        // if file does not exists, return null
        if (!file.exists()) {
            return null ;
        }

        // read file into String
        String text = file.readString();
        Array<String> wordList = new Array<String>();

        String startTag = "<s>";
        String endTag = "</s>";

        int index ;
        int wordStartIndex = 0 ;
        int wordEndIndex = 0 ;

        do {
            // find index of startTag
            index = text.indexOf(startTag, wordEndIndex) ;

            // calculate index of the word's first character
            wordStartIndex = index + startTag.length() ;

            String word ;
            // if index was -1, there were no more tags left -> end of the loop
            if (index != -1) {

                // find the end tag index and get the word between startTag and endTag
                wordEndIndex = text.indexOf(endTag, wordStartIndex) ;
                word = text.substring(wordStartIndex, wordEndIndex) ;

                // some words contains minus character, we do not want those words
                if (!word.contains("-")) {
                    wordList.add(word);
                }
            }

        } while (index != -1);


        return wordList ;
    }
}
