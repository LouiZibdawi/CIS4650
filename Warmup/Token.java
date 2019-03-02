/*
Token.java
Used to store and classify tokens created in the .flex file.
 */

class Token {

  public final static int ID = 20;
  public final static int NUM = 21;
  public final static int OPENTAG = 22;
  public final static int CLOSETAG = 23;
  public final static int HYPHENWORD = 24;
  public final static int APOSWORD = 25;
  public final static int PUNCTUATION = 26;

  public int m_type;
  public String m_value;
  public int m_line;
  public int m_column;
  
  Token (int type, String value, int line, int column) {
    m_type = type;
    m_value = value;
    m_line = line;
    m_column = column;
  }

  public String toString() {
    switch (m_type) {
      case ID:
        return "WORD(" + m_value + ")";
      case OPENTAG:
        return "OPEN-" + m_value;
      case CLOSETAG:
        return "CLOSE-" + m_value;
      case HYPHENWORD:
        return "HYPHENATED(" + m_value + ")";
      case APOSWORD:
        return "APOSTROPHIZED(" + m_value + ")";
      case NUM:
        return "NUMBER(" + m_value + ")";
      case PUNCTUATION:
        return "PUNCTUATION(" + m_value + ")";
      default:
        return "UNKNOWN(" + m_value + ")";
    }
  }
}

