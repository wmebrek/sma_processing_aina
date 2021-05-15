import os
import pandas as pd


df = pd.read_csv("aarhus_parking_address.csv",delimiter=',', header=None)
df.columns=['garagecode','city','postalcode','street','housenumber','latitude','longitude']

def cache(func):
    cache = dict()

    def cached_func(*args):
        if args in cache:
            return cache[args]
        result = func(*args)
        cache[args] = result
        return result

    return cached_func

def searchWithGarageCode(garageCode):
    return df[df['garagecode']==garageCode]

print("open parking csv")
fin = open("AarhusParkingData.stream", "rt")
print("parking sorted opened")

cached_search_result = cache(searchWithGarageCode)
#The output file in which we add the longitude and latitude columns
fout = open("AarhusParkingData0.stream", "wt")
fout.write(fin.readline().rstrip('\n')+',Point1_Lat,Point1_Long\n')
count=0
next(fin)   #we skip the first line that contains the names of fields
for line in fin:
    garageCode = line.split(",")[4]
    #print("garagecode " + garageCode)
    #garageCode = garageCode[:6]
    #print("garagecodeAfter " + garageCode)
    count+=1
    res = cached_search_result(garageCode)
    point1_lat = (res.values[0][5])
    point1_long = (res.values[0][6])
    #We write the input file line and we add the longitude and latitude values of the 2 points
    fout.write(line.rstrip('\n') + ',' +  point1_lat + ',' + point1_long + '\n')
    if(count%10000==0):
        print('processed ' + str(count) + ' lines')

fin.close()
fout.close()
