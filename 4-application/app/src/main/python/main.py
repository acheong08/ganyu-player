# pyright: reportUnknownMemberType=false, reportMissingTypeStubs=false, reportUnknownVariableType=false, reportUnusedCallResult=false
import typing
import yt_dlp
from mutagen.mp4 import MP4


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
            # "postprocessors": [
            #     {"key": "FFmpegMetadata", "add_metadata": True},
            #     {"key": "EmbedThumbnail", "already_have_thumbnail": True},
            # ],
        }
    ) as ydl:
        info = typing.cast(dict[str, str], ydl.extract_info(f"{yt_id}", download=True))

        tags = MP4(file_path).tags
        if tags is None:
            tags = MP4(file_path).add_tags()
        if tags is None:
            raise Exception("Failed to add tags")
        tags["\xa9nam"] = info["title"]
        tags["\xa9ART"] = info["uploader"]
        tags["\xa9alb"] = info.get("album", "")

        tags.save(file_path)
        return (
            info["title"],
            info["uploader"],
            info.get("album", ""),
            int(info.get("duration", 0)) * 1000,
            file_path,
        )


# if __name__ == "__main__":
# print(download("U8BlNEKq0r8", "/tmp/output"))
# print(search("never gonna give you up"))
