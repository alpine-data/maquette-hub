import uvicorn

from fastapi import FastAPI
from data_explorer.models.request import RequestBody, FormatEnum
from data_explorer.models.response import ResponseBody
from data_explorer.data_explorer import get_ds_statistics, get_so_statistics, get_profile

app = FastAPI()


@app.post("/api/statistics", response_model=ResponseBody)
async def statistics(request: RequestBody, plots: bool = False):
        if request.dataset:
            return get_ds_statistics(dataset_name=request.dataset, version=request.version, with_images=plots)
        elif request.source:
            return get_so_statistics(source_name=request.source, with_images=plots)


@app.post("/api/profile")
async def profile(request: RequestBody):
    if request.dataset:
        return get_profile(data_asset_name=request.dataset, version=request.version, format=request.format)
    elif request.source:
        return get_profile(data_asset_name=request.source, format=request.format)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
