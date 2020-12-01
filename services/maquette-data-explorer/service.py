from fastapi import FastAPI
from data_explorer.models.request import RequestBody
from data_explorer.models.response import ResponseBody
from data_explorer.data_explorer import get_statistics

app = FastAPI()


@app.post("/api/statistics/", response_model=ResponseBody)
async def statistics(request: RequestBody, plots: bool = False):
    return get_statistics(project_name=request.project, dataset_name=request.dataset, version=request.version, with_images=plots)