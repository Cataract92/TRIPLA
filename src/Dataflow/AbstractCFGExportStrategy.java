package Dataflow;

import java.io.IOException;
import java.io.Writer;

public abstract class AbstractCFGExportStrategy {

    public abstract void export(Writer writer, CFG cfg) throws IOException;

}
