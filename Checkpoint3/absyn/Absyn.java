package absyn;

abstract public class Absyn {
  public int row, col, pos;

  abstract public void accept( AbsynVisitor visitor, int level );
}
