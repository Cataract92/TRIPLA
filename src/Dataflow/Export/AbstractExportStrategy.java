/*
 * Nico Feld - 1169233
 */

package Dataflow.Export;

import Dataflow.CFG;

import java.io.IOException;
import java.io.Writer;

public abstract class AbstractExportStrategy {

    public abstract void export(Writer writer, CFG cfg) throws IOException;

}
