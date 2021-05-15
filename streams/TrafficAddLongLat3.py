import os
import pandas as pd


df = pd.read_csv("trafficMetaData.csv",delimiter=',', header=None)
df.columns=['POINT_1_STREET','DURATION_IN_SEC','POINT_1_NAME','POINT_1_CITY','POINT_2_NAME','POINT_2_LNG','POINT_2_STREET','NDT_IN_KMH','POINT_2_POSTAL_CODE','POINT_2_COUNTRY','POINT_1_STREET_NUMBER','ORGANISATION','POINT_1_LAT','POINT_2_LAT','POINT_1_POSTAL_CODE','POINT_2_STREET_NUMBER','POINT_2_CITY','extID','ROAD_TYPE','POINT_1_LNG','REPORT_ID','POINT_1_COUNTRY','DISTANCE_IN_METERS','REPORT_NAME','RBA_ID','_id']

def cache(func):
    cache = dict()

    def cached_func(*args):
        if args in cache:
            return cache[args]
        result = func(*args)
        cache[args] = result
        return result

    return cached_func

def searchWithReportId(report_id):
    return df[df['REPORT_ID']==report_id]

print("open traffic csv")
fin = open("TrafficDataSorted.stream", "rt")
print("traffic sorted opened")

cached_search_result = cache(searchWithReportId)
#The output file in which we add the longitude and latitude columns
fout = open("TrafficLongLat3.stream", "wt")
fout.write(fin.readline().rstrip('\n')+',Point1_Lat,Point1_Long,Point2_Lat,Point2_Long\n')
count=0
next(fin)   #we skip the first line that contains the names of fields
for line in fin:
    report_id = line.split(",")[8]
    report_id = report_id[:6]
    count+=1
    res = cached_search_result(report_id)
    point1_lat = (res.values[0][12])
    point1_long = (res.values[0][19])
    point2_lat = (res.values[0][13])
    point2_long = (res.values[0][5])
    #We write the input file line and we add the longitude and latitude values of the 2 points
    fout.write(line.rstrip('\n') + ',' +  point1_lat + ',' + point1_long + ',' + point2_lat + ',' + point2_long + '\n')
    if(count%10000==0):
        print('processed ' + str(count) + ' lines')

fin.close()
fout.close()
