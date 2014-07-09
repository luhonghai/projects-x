/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium.utils;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class FileUtils {

	public static String readResourceContent(String resource)
			throws IOException {
		return FileUtils.readFileContent(FileUtils.class.getClassLoader()
				.getResourceAsStream(resource));
	}

	/**
	 * read file XML
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String readFileContent(InputStream is) throws IOException {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"ISO-8859-1"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			// log.info("XML String : " + sb.toString());
			return sb.toString();

		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
					// Silent
				}
			}
		}
	}

    public static void writeProperties(File f, Properties prop) throws MojoExecutionException {
        OutputStream output = null;
        try {

            output = new FileOutputStream(f);

            prop.store(output, null);

        } catch (IOException io) {
            throw  new MojoExecutionException("Could not write properties file. Message: " + io.getMessage());
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

	public static void writeFile(String filePath, String encoding, String source)
			throws MojoExecutionException {
		writeFile(new File(filePath), encoding, source);
	}

	public static void writeFile(File file, String encoding, String source)
			throws MojoExecutionException {
		FileOutputStream fo = null;
		BufferedWriter bw = null;
		try {
			fo = new FileOutputStream(file);

			bw = new BufferedWriter(new OutputStreamWriter(fo, encoding));

			bw.write(source);
			bw.flush();
		} catch (Exception ex) {
			throw new MojoExecutionException("Could not write script " + file,
					ex);
		} finally {
			if (fo != null) {
				try {
					fo.close();
				} catch (Exception ex) {
					// silent
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception ex) {
					// silent
				}
			}
		}
	}

	public static String readFile(String filePath, String encoding)
			throws MojoExecutionException {
		return readFile(new File(filePath), encoding);
	}

	public static String readFile(File file, String encoding)
			throws MojoExecutionException {

		FileInputStream is = null;
		BufferedReader br = null;
		try {
			is = new FileInputStream(file);

			br = new BufferedReader(new InputStreamReader(is, encoding));
			StringBuffer bf = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				bf.append(line).append("\n");
			}
			return bf.toString();
		} catch (Exception ex) {
			throw new MojoExecutionException("Could not read file " + file, ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
					// silent
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (Exception ex) {
					// silent
				}
			}
		}
	}

    /**
     * Writes an embedded resource to a local file by the resource name.
     *
     * @param resourceName an embedded resource name
     * @param configFile a file we write the resource to
     * @return true when write was successful and false otherwise
     */
    public static boolean writeEmbeddedResourceToLocalFile( final String resourceName, final File configFile )
    {
        boolean result = false;

        final URL resourceUrl = FileUtils.class.getClassLoader().getResource( resourceName );

        // 1Kb buffer
        byte[] buffer = new byte[1024];
        int byteCount = 0;

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            inputStream = resourceUrl.openStream();
            outputStream = new FileOutputStream( configFile );

            while( (byteCount = inputStream.read( buffer ) ) >= 0)
            {
                outputStream.write( buffer, 0, byteCount );
            }

            // report success
            result = true;
        }
        catch (final IOException e)
        {
            //logger.error( "Failure on saving the embedded resource " + resourceName + " to the file " + configFile.getAbsolutePath(), e );
        }
        finally
        {
            if( inputStream != null )
            {
                try
                {
                    inputStream.close();
                }
                catch (final IOException e)
                {
                    //logger.warn( "Problem closing an input stream while reading data from the embedded resource " + resourceName, e );
                }
            }
            if( outputStream != null )
            {
                try
                {
                    outputStream.flush();
                    outputStream.close();
                }
                catch (final IOException e)
                {
                    //logger.warn( "Problem closing the output stream while writing the file " + configFile.getAbsolutePath(), e );
                }
            }
        }

        return result;
    }

}
