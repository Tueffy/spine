package spine

import java.io.File;
import java.util.regex.*
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.io.FileUtils

class FileService {

    static transactional = true

    /**
     * Generate a random and unique string
     * @param String extension
     * @return String
     */
	def String generateRandomUniqueFileName(String extension = null) 
	{
		String fileName = ""
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-")
		fileName += dateFormat.format(new Date()).toString()
		fileName += UUID.randomUUID().toString()
		if(extension != null)
			fileName += "." + extension
		return fileName
    }
	
	/**
	 * 
	 * @param File file
	 * @param String newName
	 * @return Boolean
	 */
	def renameFile(File file, String newName)
	{
		File newFile = new File(file.getParentFile().getPath() + "/" + file.getName())
		Boolean renamingSuccess = file.renameTo(newFile)
		System.out.println("\n" + renamingSuccess) // TODO : Don't forget to remove this, or used a better way for logging events
	}
	
	/** 
	 * Extract extension from filename 
	 * @param String fileName
	 * @return String
	 */
	def String extractExtensionFromFileName(String fileName)
	{
		int dotPosition = fileName.lastIndexOf(".");
		if(dotPosition == -1) return ""
		return fileName.substring(dotPosition + 1, fileName.length()).toString()
	}
	
	/**
	* Lowercase the extension of a file
	* @param String originalFileName
	* @return String
	*/
   private String replaceLowercasedExtension(String originalFileName)
   {
	   Pattern pattern = Pattern.compile("\\.([A-Za-z0-9]+)\$")
	   Matcher matcher = pattern.matcher(originalFileName)
	   StringBuilder stringBuilder = new StringBuilder(originalFileName)
	   while(matcher.find())
	   {
		   String buf= stringBuilder.substring(matcher.start(), matcher.end()).toLowerCase();
		   stringBuilder.replace(matcher.start(), matcher.end(), buf);
	   }
	   return stringBuilder.toString()
   }
}
