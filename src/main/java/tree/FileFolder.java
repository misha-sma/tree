package tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileFolder {
	private long idFile;
	private String fileName;
	private int idType;
	private String fileValue;
	private long idParent;

	@Override
	public String toString() {
		return idFile + ", " + fileName + ", " + idType + ", " + fileValue + ", " + idParent;
	}
}
