import java.io.*;
import absyn.*;

public class SymItem {
	public String name;
	public int type;
	public int level;
	public String params;
	public int offset;

	public SymItem(String name, int type, int level, String params, int offset) {
		this.name = name;
		this.type = type;
		this.level = level;
		this.params = params;
		this.offset = offset;
	}

	public SymItem(String name, int type, int level, String params) {
		this.name = name;
		this.type = type;
		this.level = level;
		this.params = params;
		this.offset = 10000000;
	}

	public SymItem() {
		this.name = "";
		this.type = 1;
		this.level = 0;
		this.params = "";
		this.offset = 0;
	}
}