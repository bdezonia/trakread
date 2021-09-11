/*
 * TrakVis .TRK file reader and utilities
 * 
 * Written by Barry DeZonia during September 2021
 * 
 * Based on the TrackVis TRK file format as published here:
 * 
 *   http://www.trackvis.org/docs/?subsect=fileformat
 * 
 * This code is being put in the public domain using the Unlicense LICENSE.
 * See the license details as stored in the root of this project as LICENSE. 
 */
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nom.bdezonia.trakread.TrakData;
import nom.bdezonia.trakread.TrakHeader;
import nom.bdezonia.trakread.TrakUtils;

public class Main {

	public static void main(String[] args) {
		
		String filename = "/home/bdezonia/images/nifti/DTI_Lab3_tracts.trk";
		
		boolean fileIsLittleEndian = false;
		
		try {
			
			fileIsLittleEndian = TrakUtils.fileIsLittleEndian(filename);
		
		} catch (IOException e) {
		
			System.err.println("EXITING: AN IO EXCEPTION OCCURRED: " + e.getMessage());

			System.exit(1);  // return error condition
		}
		
		File file = new File(filename);
		
		FileInputStream fileStream = null;
		
		try {
		
			fileStream = new FileInputStream(file);
		
		} catch (FileNotFoundException e) {
		
			System.err.println("EXITING: FILE NOT FOUND: " + filename);
			
			System.exit(2);  // return error condition
		}
		
		DataInputStream dataStream = new DataInputStream(fileStream);

		TrakHeader header = new TrakHeader();
		
		try {
		
			header = TrakHeader.readFromSource(dataStream, fileIsLittleEndian);
			
			// report header vals
			
			header.print(System.out);
			
			// and then read data if desired

			TrakData.printSummary(System.out, dataStream, fileIsLittleEndian, header);
			
			dataStream.close();
			
		} catch (IOException e) {

			System.err.println("EXITING: COULD NOT READ FILE HEADER: " + e.getMessage());
			
			System.exit(3);  // return error condition
		}
		
		// let OS know things are great
		
		System.exit(0);
	}
}
