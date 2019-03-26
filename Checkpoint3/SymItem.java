import java.io.*;
import absyn.*;

public class SymItem {
	public String name;
	public int type;
	public int level;
	public String params;

	public SymItem(String name, int type, int level, String params) {
		this.name = name;
		this.type = type;
		this.level = level;
		this.params = params;
	}

	public SymItem() {
		this.name = "";
		this.type = 1;
		this.level = 0;
		this.params = "";
	}
}