import csv
import yaml
import sys
import json
from csv import DictReader


def format(s):
    return '"{}"'.format(s)

def is_selected(value):
    return value == 'x' or value == 'X'

def check_empty(value):
    if not value or value == '':
        return '""'

def append_interface(value, name, interfaces):
    if is_selected(value):
        interfaces.append(name)

def loadCsv(file_name):
    elements = []
    with open(file_name) as file:
        reader = DictReader(file, delimiter="\t", quotechar='"')
        # next(reader)
        for row in reader:
            if is_selected(row['Skip']):
                continue
            del row['Skip']
            row['Type'] = int(row['Type'])
            try:
                row['NumberOfFixedOctets'] = int(row['NumberOfFixedOctets'])
            except ValueError:
                row['NumberOfFixedOctets'] = -1

            elements.append(row)

    return elements



def render(elements, template):
    with open(template) as f:
        content = f.read()
        liq = Liquid(content)
        res = liq.render(elements = elements)

    return res

if __name__ == '__main__':
    file = '/home/jonas/development/snice/snice-codecs-base/codec-gtp-codegen/src/main/resources/raw/gtpv1_information_elements.tsv'
    elements = loadCsv(file)
    print(yaml.dump(elements))
    # with open('messages_types.yml', 'w') as ie:
        # ie.write(yaml.dump(elements, sort_keys=False))
    # sys.exit(1)

    #print(json.dumps(elements, indent = 3) )

