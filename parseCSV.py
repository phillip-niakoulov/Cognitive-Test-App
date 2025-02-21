import json

read_path = 'emotivCorsiBlock.csv'
write_path = 'emotivCorsiBlock_.csv'

with open(read_path, 'r') as file:
    with open(write_path, 'a') as new_file:
        for line in file:
            line = line.strip()
            if line:
                data = json.loads(line)
#                 timestamp,t7,t8,pz,af3,af4
                if data['Type'] == "EEG":
                    timestamp = data['Time']
                    t7 = data['T7']
                    t8 = data['T8']
                    pz = data['Pz']
                    af3 = data['AF3']
                    af4 = data['AF4']
                    new_file.write(f"{timestamp},{t7},{t8},{pz},{af3},{af4}\n")
#                 timestamp,actionEye,powerUpperFace,actionUpperFace,powerLowerFace,actionLowerFace
                if data['Type'] == "FACE":
                    timestamp = data['time']
                    actionEye = data['actionEye']
                    powerUpperFace = data['powerUpperFace']
                    actionUpperFace = data['actionUpperFace']
                    powerLowerFace = data['powerLowerFace']
                    actionLowerFace = data['actionLowerFace']
                    new_file.write(f"{timestamp},{actionEye},{powerUpperFace},{actionUpperFace},{powerLowerFace},{actionLowerFace}\n")
#                 timestamp,interest,activeInterest,relaxation,activeRelaxation,engagement,activeEngagement,excitement,activeExcitement,stress,activeStress
                if data['Type'] == 'AFFECT':
                    timestamp = data['Time']
                    interest = data['Interest']
                    activeInterest = data['Active Interest']
                    relaxation = data['Relaxation']
                    activeRelaxation = data['Active Relaxation']
                    engagement = data['Engagement']
                    activeEngagement = data['Active Engagement']
                    excitement = data['Excitement']
                    activeExcitement = data['Active Excitement']
                    stress = data['Stress']
                    activeStress = data['Active Stress']
                    new_file.write(f"{timestamp},{interest},{activeInterest},{relaxation},{activeRelaxation},{engagement},{activeEngagement},{excitement},{activeExcitement},{stress},{activeStress}\n")
#                 timestamp,P,A,D
                if data['Type'] == 'PAD':
                    timestamp = data['time']
                    p = data['P']
                    a = data['A']
                    d = data['D']
                    new_file.write(f"{timestamp},{p},{a},{d}\n")