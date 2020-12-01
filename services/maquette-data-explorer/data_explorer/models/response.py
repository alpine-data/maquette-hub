from enum import Enum
from typing import Optional,List, Dict, Union
from pydantic import BaseModel

class Type(str,Enum):
    numeric = "numeric"
    text = "text"

class Stats(BaseModel):
    valid: tuple
    mismatched: tuple
    missing: tuple
    details: List[List[List]]

class Column(BaseModel):
    name: str
    type: Type
    stats: Stats
    image: Optional[str]

class ResponseBody(BaseModel):
    columns: List[Column]
    project: str
    dataset: str
    version: str