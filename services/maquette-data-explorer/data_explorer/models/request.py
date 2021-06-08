from pydantic import BaseModel
from typing import Optional
from enum import Enum

class FormatEnum(str, Enum):
    html = 'html'
    json = 'json'
    statistic = 'statistic'

class RequestBody(BaseModel):
    dataset: Optional[str]
    source: Optional[str]
    version: Optional[str]
    format: Optional[FormatEnum]