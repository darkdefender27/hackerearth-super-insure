from configparser import ConfigParser

def read(filename='config/config.ini', section='mysql'):
    """ Read the config file available in the directory

    """
    parser = ConfigParser()
    parser.read(filename)

    section_value = {}
    if parser.has_section(section):
       items = parser.items(section)
       for item in items:
           section_value[str(item[0])] = str(item[1])
    else:
        raise Exception('{0} not found in the {1} file'.format(section, filename))

    return section_value
