from datetime import datetime

#TODO replace any useage with centralized API function, issue #16
def generate_unique_name(name: str) -> str:
    return name+"_"+ datetime.now().strftime("%Y:%m:%d:%H:%M:%S")