from pydantic import BaseModel

class RequestBody(BaseModel):
    project: str
    dataset: str
    version: str