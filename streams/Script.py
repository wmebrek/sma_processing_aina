import os
# Path to TrafficData streams
path = "./TrafficData/"

streams = os.listdir(path)
streams.sort()
fout = open("TrafficDataAggregated.stream", "wt")
fin = open(path+streams[0], "rt")
fout.write(fin.readline())
fin.close()
for stream in streams:
  #input file
  fin = open(path+stream, "rt")
  next(fin)
  for line in fin:
    fout.write(line)
  fin.close()

fout.close()

#Sorting the aggregated file
def mysort(line):
    return line.split(",")[5]

fin = open("TrafficDataAggregated.stream", "rt")
fout = open("TrafficDataSorted.stream", "wt")
fout.write(fin.readline())
next(fin)
text = fin.readlines()
for line in sorted(text, key=mysort):
    fout.write(line)
fin.close()
fout.close()