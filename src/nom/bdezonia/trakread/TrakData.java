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
package nom.bdezonia.trakread;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;

public class TrakData {

	// This code shows anyone who wants to compile the input data into something a proper
	// way of going about it.
	
	public static void printSummary(PrintStream stream, DataInput source, boolean dataIsLittleEndian, TrakHeader header) throws IOException {
		
		stream.println("Scanning all tracks for summarization. This may take a while ...");
		
		final int DIFFERENCES_EXIST = -5000;
		
		int numScalarsPerPoint = header.n_scalars + 3;
		
		int numPropertiesPerTrack = header.n_properties;
		
		int tracksSoFar = 0;
		
		int trackSize = -1;
		
		while (true) {
			try {
		
				// read a track
				
				int numPointsInTrack = TrakUtils.readInt(source, dataIsLittleEndian);
				
				if (trackSize == -1) {
				
					trackSize = numPointsInTrack;
				}
				else {
				
					if (trackSize != DIFFERENCES_EXIST && numPointsInTrack != trackSize)
					
						trackSize = DIFFERENCES_EXIST;
				}
				
				// read the track a point at a time
				
				for (int i = 0; i < numPointsInTrack; i++) {
					
					for (int j = 0; j < numScalarsPerPoint; j++) {
					
						// throwing away data for now
				
						TrakUtils.readFloat(source, dataIsLittleEndian);
					}
				}
				
				for (int j = 0; j < numPropertiesPerTrack; j++) {
					
					// throwing away data for now
					
					TrakUtils.readFloat(source, dataIsLittleEndian);
				}
				
				// stream.println("Just read track "+tracksSoFar);
				
				tracksSoFar++;
				
			} catch (Exception e) {

				if (trackSize == DIFFERENCES_EXIST) {
					
					stream.println("Source has a varying number of points per track.");
				}
				else {
					
					stream.println("Source has a fixed number of points per track: " + trackSize);
				}

				System.out.println("Scanned "+tracksSoFar+" tracks.");
				
				return;
			}
		}
	}
}
