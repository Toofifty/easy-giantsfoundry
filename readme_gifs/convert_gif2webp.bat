# convert all gif files in the current directory to webp using ffmpeg
# requires ffmpeg and webp

for %%i in (*.gif) do D:\ffmpeg -i "%%i" -c:v libwebp -loop 0 -pix_fmt yuva420p "%%~ni.webp"

