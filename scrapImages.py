import os
import requests
from bs4 import BeautifulSoup

url = "https://unsplash.com/s/collections/MARVEL"

response = requests.get(url)
soup = BeautifulSoup(response.content, "html.parser")

collection_links = []
for link in soup.find_all("a", class_="A3ryi"):
    collection_links.append(link["href"])
total_count = 0
for each_link in collection_links:
    # print("https://unsplash.com"+each_link)
    each_url ="https://unsplash.com"+each_link

    # Send HTTP request to the URL and get the HTML content
    response = requests.get(each_url)
    html_content = response.content

    # Parse the HTML content using BeautifulSoup
    soup = BeautifulSoup(html_content, "html.parser")

    #scrap the collection Name
    collection_name = each_url.split("/")[-1].replace("-","").title()
    collection_name.replace("\\"," ").title()

    # Create folder to save images
    if not os.path.exists(collection_name):
        os.makedirs(collection_name)
    # Find all the <img> tags in the HTML content
    img_tags = soup.find_all("img")
    print(f"Extracting Images from the Collection{collection_name}...")
    i = 0
    count = 0
    # Loop through all the <img> tags and download the images
    for img in img_tags:
        if i < 2:
            i+=1
            continue
        img_url = img.get("src")
        img_alt = img.get("alt")

        if img_url and img_alt and "profile" not in img_alt:
            # Get the filename of the image from the "alt" attribute
            filename = os.path.join(collection_name, img_alt + ".png")

            if not os.path.exists(filename):
                with open(filename, "wb") as f:
                    f.write(requests.get(img_url).content)
                    count += 1
                    total_count += 1

    print(f"Total Images Extracted {count} from the collection {collection_name}")

print(f"Total Images extracted : {total_count}")
