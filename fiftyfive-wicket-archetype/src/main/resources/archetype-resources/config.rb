# Require any additional compass plugins here.
require 'compass-colors'
require 'sassy-buttons'

# Set this to the root of your project when deployed:
http_path = "/"

_resources = "src/main/resources/${package.replace('.','/')}"
css_dir = _resources + "/styles-compiled"
sass_dir = _resources + "/styles"
additional_import_paths = [_resources + "/styles/basics", _resources + "/styles/shared"]

images_dir = _resources + "/images"
javascripts_dir = _resources + "/scripts"

relative_assets = true

output_style = :expanded
line_comments = false
