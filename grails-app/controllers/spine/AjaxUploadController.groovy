package spine

import java.awt.Image
import java.awt.Toolkit

import grails.converters.JSON
import groovyx.net.http.ContentType;
import static org.codehaus.groovy.grails.commons.ConfigurationHolder.config as Config

import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.codehaus.groovy.grails.commons.GrailsResourceUtils;
import org.codehaus.groovy.grails.web.context.ServletContextHolder;
import org.springframework.http.HttpStatus
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import javax.swing.ImageIcon;
import javax.imageio.ImageIO
import pl.burningice.plugins.image.BurningImageService

import spine.FileService


/**
 * @todo: Sometimes, upload a cropping does work properly
 * @todo: Looks like there si too much logic in the controller
 */

class AjaxUploadController extends uk.co.desirableobjects.ajaxuploader.AjaxUploadController  {

	BurningImageService burningImageService
	FileService fileService
	
	/**
	 * Overwrite the upload method from the default plug-in controller
	 */
	def upload = {
		try {

			String uploadedDir = 'uploads/tmp/'
			String uploadedFileName =  params.qqfile
			uploadedFileName = fileService.generateRandomUniqueFileName() + "." + fileService.extractExtensionFromFileName(uploadedFileName).toLowerCase()
			String uploadedFullFileDir = ServletContextHolder.servletContext.getRealPath('/') + uploadedDir
//			File uploaded = new File(uploadedFullFileDir + uploadedFileName)
			File uploaded = new File(uploadedFullFileDir + uploadedFileName)
			InputStream inputStream = selectInputStream(request)

			// Let's upload picture
			ajaxUploaderService.upload(inputStream, uploaded)
			
			// Do some stuff on the picture
			int test;
			burningImageService.doWith(uploadedFullFileDir + uploadedFileName, uploadedFullFileDir).execute {
				// We use an ImageIcon here, to make sure the image is actually loaded
				ImageIcon pre_image = new ImageIcon(uploadedFullFileDir + uploadedFileName)
				Image image = pre_image.getImage()
				image.flush()
				int width = image.getWidth(null);
				int height = image.getHeight(null);
				if(width > 500)
				{
					float ratio = 500 / width
					int newWidth = width * ratio
					int newHeight = height * ratio
					it.scaleAccurate(newWidth, newHeight)
				}
				test = width;
			}
			
			// Rename the uploaded file
			fileService.renameFile(uploaded, fileService.generateRandomUniqueFileName())

			return render(text: [success:true, filename:fileService.replaceLowercasedExtension(uploadedFileName), dir:uploadedDir, width:test, tmp:fileService.generateRandomUniqueFileName()] as JSON, contentType:'text/json')

		} catch (FileUploadException e) {

			log.error("Failed to upload file.", e)
			return render(text: [success:false] as JSON, contentType:'text/json')

		}

	}
	
}
