/**
 * File: $HeadURL$
 * Revision: $Rev$
 * Last modified: $Date$
 * Last modified by: $Author$
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contacting the authors:
 *   Mario Arias:               mario.arias@deri.org
 *   Javier D. Fernandez:       jfergar@infor.uva.es
 *   Miguel A. Martinez-Prieto: migumar2@infor.uva.es
 *   Alejandro Andres:          fuzzy.alej@gmail.com
 */
package org.rdfhdt.hdt.tools;

import java.io.PrintStream;
import java.util.List;

import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;


/**
 * @author mario.arias
 *
 */
public class HDT2RDF implements ProgressListener {
	@Parameter(description = "<input RDF> <output HDT>")
	public List<String> parameters = Lists.newArrayList();
	
	public String hdtInput = null;
	public String rdfOutput = null;

	public void execute() throws Exception {
		
		PrintStream out = null;
		if (rdfOutput.equals("stdout")){
			out = System.out;
		} else {
			out = new PrintStream(rdfOutput, "UTF-8");
		}
		
		HDT hdt = HDTManager.mapHDT(hdtInput, this);

		IteratorTripleString it = hdt.search("","","");
		StringBuilder build = new StringBuilder(1024);
		while(it.hasNext()) {
			TripleString triple = it.next();
			build.delete(0, build.length());
			triple.dumpNtriple(build);
			out.print(build);
		}
		if(!rdfOutput.equals("stdout")) {
			out.close();
		}
	}

	/* (non-Javadoc)
	 * @see hdt.ProgressListener#notifyProgress(float, java.lang.String)
	 */
	@Override
	public void notifyProgress(float level, String message) {
		//System.out.println(message + "\t"+ Float.toString(level));
	}

	public static void main(String[] args) throws Throwable {
		HDT2RDF hdt2rdf = new HDT2RDF();
		JCommander com = new JCommander(hdt2rdf, args);
		com.setProgramName("hdt2rdf");

		try {
			hdt2rdf.hdtInput = hdt2rdf.parameters.get(0);
		} catch (Exception e){
			com.usage();
			System.exit(1);
		}

		try {
			hdt2rdf.rdfOutput = hdt2rdf.parameters.get(1);
		} catch (Exception e){
			hdt2rdf.rdfOutput = "stdout";
		}
		System.err.println("Converting "+hdt2rdf.hdtInput+" to RDF on "+hdt2rdf.rdfOutput);
		
		hdt2rdf.execute();
	}


}