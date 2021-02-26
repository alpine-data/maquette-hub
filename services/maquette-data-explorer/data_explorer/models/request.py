from pydantic import BaseModel
from typing import Optional

class RequestBody(BaseModel):
    dataset: Optional[str]
    source: Optional[str]
    version: Optional[str]