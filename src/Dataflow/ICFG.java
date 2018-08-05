package Dataflow;

public interface ICFG {

    CFGVertex getIn();
    CFGVertex getOut();
    void setIn(CFGVertex in);
    void setOut(CFGVertex out);
    void export(AbstractCFGExportStrategy exportStrategy);

}
