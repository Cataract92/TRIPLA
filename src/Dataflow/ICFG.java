package Dataflow;

public interface ICFG {

    AbstractCFGVertex getIn();
    AbstractCFGVertex getOut();
    void setIn(AbstractCFGVertex in);
    void setOut(AbstractCFGVertex out);
    void export(AbstractCFGExportStrategy exportStrategy);

}
