# pyright: reportUnknownMemberType=false, reportMissingTypeStubs=false, reportUnknownVariableType=false, reportUnusedCallResult=false
import typing
import yt_dlp


def main():
    return "Hello, World!"


# Download audio file with metadata
# Returns a tuple with the following elements:
# - Title
# - Artist
# - Album
# - File path
def download(yt_id: str, tmp_dir: str) -> tuple[str, str, str, int, str]:
    file_path = f"{tmp_dir}/{yt_id}.m4a"
    with yt_dlp.YoutubeDL(
        {
            "format": "m4a",
            "outtmpl": file_path,
        }
    ) as ydl:
        info = typing.cast(dict[str, str], ydl.extract_info(f"{yt_id}", download=True))
        return (
            info["title"],
            info["uploader"],
            info.get("album", ""),
            int(info.get("duration", 0)) * 1000,
            file_path,
        )


if __name__ == "__main__":
    print(download("U8BlNEKq0r8", "/tmp/output"))
