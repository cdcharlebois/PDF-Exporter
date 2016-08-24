package pdf_exporter.lib;

import java.util.ArrayList;
import java.util.List;

public class MXPDField {
	private String name;
	private String value;
	private List<MXPDField> children;
	private String parent;
	private String type;
	private int length;
	private int page;
	
	public MXPDField(String name, String value, String parent, String type, int length, int page) {
		super();
		this.name = name;
		this.value = value;
		this.parent = parent;
		this.type = type;
		this.length = length;
		this.page = page;
		
		this.children = new ArrayList<MXPDField>();
	}
	public MXPDField(String name, String value, String parent, String type, int length, int page, List<MXPDField> children) {
		super();
		this.name = name;
		this.value = value;
		this.parent = parent;
		this.type = type;
		this.length = length;
		this.page = page;
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void addChild(MXPDField child)
	{
		children.add(child);
	}
	public List<MXPDField> getChildren() {
		return children;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getPage() {
		return page;
	}
	public void setpage(int page) {
		this.page = page;
	}
	@Override public String toString() {
		return name + "\r\n" + value + "\r\n" + parent + "\r\n" + type + "\r\n\r\n";
	}
}
