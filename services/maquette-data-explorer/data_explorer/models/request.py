from pydantic import BaseModel

class RequestBody(BaseModel):
    dataset: str
    version: str