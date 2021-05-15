import os
import pandas as pd
import math
from math import cos, sin, atan2,sqrt,asin,pi

def center(long1,lat1,long2,lat2):
    x1 = cos(lat1) * cos(long1)
    y1 = cos(lat1) * sin(long1)
    z1 = sin(lat1)

    x2 = cos(lat2) * cos(long2)
    y2 = cos(lat2) * sin(long2)
    z2 = sin(lat2)

    #Compute average x, y and z coordinates.
    x = (x1 + x2)/2
    y = (y1 + y2) / 2
    z = (z1 + z2) / 2

    Lon = atan2(y, x)
    Hyp = sqrt(x * x + y * y)
    Lat = atan2(z, Hyp)
    return Lon,Lat


def distance(lat1, lon1, lat2, lon2):
    p = pi/180
    a = 0.5 - cos((lat2-lat1)*p)/2 + cos(lat1*p) * cos(lat2*p) * (1-cos((lon2-lon1)*p))/2
    return 12742 * asin(sqrt(a)) #2*R*asin...


df = pd.read_csv("trafficMetaData.csv",delimiter=',', header=None)
df.columns=['POINT_1_STREET','DURATION_IN_SEC','POINT_1_NAME','POINT_1_CITY','POINT_2_NAME','POINT_2_LNG','POINT_2_STREET','NDT_IN_KMH','POINT_2_POSTAL_CODE','POINT_2_COUNTRY','POINT_1_STREET_NUMBER','ORGANISATION','POINT_1_LAT','POINT_2_LAT','POINT_1_POSTAL_CODE','POINT_2_STREET_NUMBER','POINT_2_CITY','extID','ROAD_TYPE','POINT_1_LNG','REPORT_ID','POINT_1_COUNTRY','DISTANCE_IN_METERS','REPORT_NAME','RBA_ID','_id']

print("traffic csv opened")
f1 = open("AarhusTrafficData0.stream", "rt")
print("traffic sorted opened")
next(f1)

point1_lat = []
point1_long = []

point2_lat = []
point2_long = []

count=0
for line in f1:
    report_id = line.split(",")[8]
    report_id = report_id[:6]

    if(len(df[df['REPORT_ID']==report_id].values)>0):
        count+=1
        point1_lat.append(df[df['REPORT_ID']==report_id].values[0][12])
        point1_long.append(df[df['REPORT_ID']==report_id].values[0][19])
        point2_lat.append(df[df['REPORT_ID']==report_id].values[0][13])
        point2_long.append(df[df['REPORT_ID']==report_id].values[0][5])
        if(count%1000==0):
            print(count)
        if(count==10000):
            break;






f1.close()

df2 = pd.read_csv("aarhus_parking_address.csv",delimiter=',', header=None)
df2.columns=['garagecode','city','postalcode','street','housenumber','latitude','longitude']

nearest_parking = []
for i in range(len(point1_lat)):
    parking_distance = []
    center_long,center_lat = center(float(point1_long[i]),float(point1_lat[i]),float(point2_long[i]),float(point2_lat[i]))
    for j in range(1,9):
        parking_distance.append(distance(float(center_lat), float(center_long), float(df2.values[j][5]), float(df2.values[j][6])))
    nearest_parking.append(df2.values[parking_distance.index(min(parking_distance))][0])
    if(i%100==0):
        print(i)






















