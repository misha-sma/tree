package tree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeDao {
	public static final String GET_FILES_FOLDERS_SQL = "WITH RECURSIVE ppp(id_file, id_parent, file_name) AS ("
			+ "    SELECT id_file, id_parent, file_name, id_type, file_value FROM files WHERE id_file = ?" + "  UNION"
			+ "    SELECT files.id_file, files.id_parent, files.file_name, files.id_type, files.file_value"
			+ "    FROM ppp, files" + "    WHERE files.id_parent = ppp.id_file" + "  )" + "  SELECT * FROM ppp";

	public static final String INSERT_FILE_SQL = "insert into files(file_name, id_type, file_value, id_parent) values(?, ?, ?, ?) returning id_file";

	public static void copyFolder(long idFolder, long idFolderDest) {
		List<FileFolder> files = getFilesFolders(idFolder);
		if (files == null || files.isEmpty()) {
			return;
		}
		FileFolder rootFolder = files.get(0);
		rootFolder.setIdParent(idFolderDest);
		createFileFolder(rootFolder);
		Map<Long, Long> idsMap = new HashMap<Long, Long>();
		idsMap.put(idFolder, rootFolder.getIdFile());
		List<FileFolder> filesCur = new ArrayList<FileFolder>();
		for (int i = 1; i < files.size(); ++i) {
			FileFolder file = files.get(i);
			if (idsMap.containsKey(file.getIdParent())) {
				file.setIdParent(idsMap.get(file.getIdParent()));
				filesCur.add(file);
			} else {
				idsMap = createFileFolders(filesCur);
				filesCur = new ArrayList<FileFolder>();
				file.setIdParent(idsMap.get(file.getIdParent()));
				filesCur.add(file);
			}
		}
		createFileFolders(filesCur);
	}

	public static Map<Long, Long> createFileFolders(List<FileFolder> files) {
		if (files == null || files.isEmpty()) {
			return null;
		}
		Map<Long, Long> idsMap = new HashMap<Long, Long>();
		try (Connection con = DataSource.getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_FILE_SQL, Statement.RETURN_GENERATED_KEYS)) {
			for (FileFolder file : files) {
				ps.setString(1, file.getFileName());
				ps.setInt(2, file.getIdType());
				ps.setString(3, file.getFileValue());
				ps.setLong(4, file.getIdParent());
				ps.addBatch();
			}
			ps.executeBatch();
			ResultSet rs = ps.getGeneratedKeys();
			int i = 0;
			while (rs.next()) {
				long idFileNew = rs.getLong(1);
				FileFolder file = files.get(i);
				idsMap.put(file.getIdFile(), idFileNew);
				file.setIdFile(idFileNew);
				++i;
			}
			rs.close();
			if (i != files.size()) {
				System.err.println("Unsaved files");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return idsMap;
	}

	public static void createFileFolder(FileFolder file) {
		try (Connection con = DataSource.getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_FILE_SQL)) {
			ps.setString(1, file.getFileName());
			ps.setInt(2, file.getIdType());
			ps.setString(3, file.getFileValue());
			ps.setLong(4, file.getIdParent());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				long idFile = rs.getLong("id_file");
				file.setIdFile(idFile);
			} else {
				file.setIdFile(-1);
			}
			rs.close();
		} catch (SQLException e) {
			file.setIdFile(-1);
			e.printStackTrace();
		}
	}

	public static List<FileFolder> getFilesFolders(long idFolder) {
		List<FileFolder> result = new ArrayList<FileFolder>();
		try (Connection con = DataSource.getConnection();
				PreparedStatement ps = con.prepareStatement(GET_FILES_FOLDERS_SQL)) {
			ps.setLong(1, idFolder);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				long idFile = rs.getLong("id_file");
				long idParent = rs.getLong("id_parent");
				String fileName = rs.getString("file_name");
				int idType = rs.getInt("id_type");
				String fileValue = rs.getString("file_value");
				FileFolder file = new FileFolder(idFile, fileName, idType, fileValue, idParent);
				result.add(file);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		copyFolder(1, 2);
		System.out.println("ENNNDDDDD");
	}

	public static void main3(String[] args) {
		createFileFolder(new FileFolder(-5, "file_name_ttt", 1, "vvvvalue", 2));
		// System.out.println("id=" + id);
	}

	public static void main2(String[] args) {
		List<FileFolder> files = getFilesFolders(1);
		for (FileFolder file : files) {
			System.out.println(file);
		}
	}

}
