package maquette.adapters.dataexplorer;

import maquette.datashop.providers.databases.ports.DatabaseDataExplorer;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;

public interface DataExplorer extends DatasetDataExplorer, DatabaseDataExplorer {
}
