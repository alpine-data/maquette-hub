from fastapi import FastAPI
from data_explorer.models.request import RequestBody
from data_explorer.models.response import ResponseBody
from data_explorer.data_explorer import get_ds_statistics, get_so_statistics

app = FastAPI()


@app.post("/api/statistics", response_model=ResponseBody)
async def statistics(request: RequestBody, plots: bool = False):
    if request.dataset:
        return get_ds_statistics(dataset_name=request.dataset, version=request.version, with_images=plots)
    elif request.source:
        return get_so_statistics(source_name=request.source, with_images=plots)