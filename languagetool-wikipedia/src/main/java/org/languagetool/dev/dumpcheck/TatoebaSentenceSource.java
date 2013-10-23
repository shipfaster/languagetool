/* LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.dev.dumpcheck;

import org.languagetool.Language;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Provides access to the sentences of a Tatoeba (http://tatoeba.org) text
 * file that has already been filtered to contain only one language,
 * @since 2.4
 */
class TatoebaSentenceSource extends SentenceSource {

  private final List<String> sentences;
  private final Scanner scanner;
  
  TatoebaSentenceSource(InputStream textInput, Language language) {
    super(language);
    scanner = new Scanner(textInput);
    sentences = new ArrayList<>();
  }

  @Override
  public boolean hasNext() {
    fillSentences();
    return sentences.size() > 0;
  }

  @Override
  public Sentence next() {
    fillSentences();
    if (sentences.size() == 0) {
      throw new NoSuchElementException();
    }
    return new Sentence(sentences.remove(0), getSource(), "<Tatoeba>", "http://tatoeba.org");
  }

  @Override
  public String getSource() {
    return "tatoeba";
  }

  private void fillSentences() {
    while (sentences.size() == 0 && scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] parts = line.split("\t");
      if (parts.length != 3) {
        continue;
      }
      String sentence = parts[2];  // actually it's sometimes two (short) sentences, but anyway...
      if (acceptSentence(sentence)) {
        sentences.add(sentence);
      }
    }
  }

}
