const { merge } = require('webpack-merge');

const common = require('./webpack.common.js');

/* Used in order to make webpack automatically generate an html file
 and add the script tag to it pointing to the ouput bundled javascript file */
 const HtmlWebpackPlugin = require('html-webpack-plugin');
 /*This plugin extracts CSS into separate files. By default, the MiniCssExtractPlugin  generates one CSS file per JavaScript entry point.Since only one entry point is declared in my config so only one css file is generated and emitted to the dist(build output folder) folder and its name is by default set to <entry_point_file_name>.css(in my case it will be index.css as entry point file name is index.js). This css file will contain all the css rules for all the css files imported within each module(javascript file). You can change the file name to be used for the emitted css using the filename option for this plugin. It supports On-Demand-Loading of CSS and SourceMaps. For the various different plugin options visit the plugin's official pages.This plugin works along side the HtmlWebpackPlugin, the HtmlWebpackPlugin adds link tags for each css file emitted by this plugin in the head tag of the emitted html file. */
 const MiniCssExtractPlugin = require("mini-css-extract-plugin");
 /*For minifying the css files generated/emitted by the MiniCssExtractPlugin.This plugin uses cssnano to optimize and minify your CSS.*/
 const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");


module.exports = merge(common, {

  mode: 'production',
  devtool: 'source-map',
 

   /* Specifying loaders and asset modules (inbuilt loaders) to use */
    module: {
      rules: [
        ...common.module.rules,
        {
          test: /\.css$/i,
          use: [
            {
              loader: MiniCssExtractPlugin.loader,
              options: {

              }
            },
            {
              loader:"css-loader",
              options: {
    
                /*The option importLoaders allows you to configure how many loaders before css-loader should be applied to @import(css at rule) resources and CSS modules/ICSS imports. 
                 // 0 => no loaders (default);
                  // 1 => postcss-loader;
                  // 2 => postcss-loader, sass-loader*/
                  // importLoaders: 1,
                  
                /* This enables compilation for the css modules specification
                for all the css files. Basically the css files will be preprocessed by the css modules preprocesser that is part of the css-loader package. By default (if modules property is not declared) the value of modules is undefined due to which only filenames ending with .module.css are compiled as css modules.(eg style.module.css). Set modules: true or set modules to an object with mode property set to a value of "local" to enable css modules preprocessing for all files ending with .css (all css files). By default or if you set modules: true or modules: "local", all the
                styles declared within a css file (or .module.css file when modules property is not specified) will be local to that
                specific javascript module(file) in which it is imported.
                There is no need to use webpack's :local(.classname) in order 
                to enforce this. If you want to  declare a selector that will work globally you use :global(.classname). Css-loader will
                assign a unique name via a hashing function to each local class whereas it won't do so for global classes.  */
                modules: {
                  /* Setting mode property ot "local" ensures that all the selectors declared in the css files are local by default i.e., treated as having been written as
                  :local(<selector>). If you want to declare global styles then you will have to explicity write :global(<selector>) */
                  mode: "local",
            
                  /*This ensure thats css modules preprocessing will only be enabled for files having exactly 2 parts separated by a dot and the second part is css.I had to add this to disable css module preprocessing for file names like "tailwind.import.css" so that i could import the tailwind
                  classes without the utility class names being converted to the hash format(see localIdentName property)*/
                  auto: /^([^\.]+)\.css$/,
                  
                  /* Used in order to change the format of the auto generated class and id
                hashes. Only set this property for development, don't set for production */
                localIdentName: "[path][name]__[local]--[hash:base64]",

    
                },
    
                /*Default: depends on the compiler.devtool value
    By default generation of source maps depends on the devtool option. All values enable source map generation except eval and false value.
                Mentioned in MiniCssExtractPlugin page:-Source maps works only for source-map/nosources-source-map/hidden-nosources-source-map/hidden-source-map values because CSS only supports source maps with the sourceMappingURL comment (i.e. //# sourceMappingURL=style.css.map). If you need set devtool to another value you can enable source maps generation for extracted CSS using sourceMap: true for css-loader.*/
                //sourceMap: true,
    
                
                
    
                
              }
            },
          ]
        },


        {
          test: /\.(?:js|mjs|cjs)$/,
          exclude: {
            // Exclude libraries in node_modules ...
            and: [/node_modules/], 
            /*Except for a few of them that needs to be transpiled because they use modern syntax (not already transpiled before) */
            not: []
          },
          use: {
            loader: 'babel-loader',
            /*see Babel's official website for a detailed reference 
            of Babel's config options */
            options: {
              /*These two cache- options are part of babel-loader's config
              options and not part of babel's config options. */
              cacheDirectory: true,
              cacheCompression: false, //default is true
  
              /*A preset is a set of plugins.Preset ordering is reversed (last to first). eg: "presets": ["a", "b", "c"] will
              will run in the following order: c, b, then a.*/
              presets: [
                ['@babel/preset-env',{
                  /*Outputs to console.log the polyfills and transform plugins enabled by preset-env and, if applicable, which one of your targets that needed it.*/
                  // debug: true,
  
                  /*This option enables a new plugin that replaces the import "core-js/stable"; and require("core-js"); statements with individual imports to different core-js entry points based on environment. (based on targets property or browserlist(don't know what this is))*/
                  useBuiltIns: "entry",
                  corejs: "3.39.0"
                }],
                ['@babel/preset-react',{
                  /*This toggles behavior specific to development, such as adding __source and __self to the jsx tags */
                  // development: true,
  
                  /*Decides which runtime to use.automatic auto imports the functions that JSX transpiles to. classic does not automatic import anything.defaults to classic */
                  // runtime: automatic,
  
                  /*Replace the function used when compiling JSX expressions. It should be a qualified name (e.g. React.createElement) or an identifier (e.g. createElement).
                  string, defaults to React.createElement.*/
                  // pragma: ,
  
                  /*Replace the component used when compiling JSX fragments. It should be a valid JSX tag name.string, defaults to React.Fragment.*/
                  // pragmaFrag: ,
                }]  
              ],
  
              /*When no targets are specified: Babel will assume you are targeting the oldest browsers possible. For example, @babel/preset-env will transform all ES2015-ES2020 code to be ES5 compatible.We recommend setting targets to reduce the output code size. Supported value types: string | Array<string> | 
              { [string]: string }*/
              //targets: ,
  
              //don't know if this is required
              //sourceMaps: true
  
  
            }
          }
        },
      ]
    },

    optimization: {
      minimizer: [
        // For webpack@5 you can use the `...` syntax to extend existing minimizers (i.e. `terser-webpack-plugin`), uncomment the next line
        `...`,
        new CssMinimizerPlugin({
          /*Type: Function<(warning, file, source) -> Boolean> Default: () => true
          Allow filtering css-minimizer warnings (By default cssnano). Return true to keep the warning, a falsy value (false/null/undefined) otherwise.*/
          //warningsFilter: (warning, file, source) => { },
        }),
      ],
    },

  plugins: [
      new MiniCssExtractPlugin({
        /*For projects where css ordering has been mitigated through consistent use of scoping or naming conventions, such as CSS Modules, the css order warnings can be disabled by setting the ignoreOrder flag to true for the plugin.*/
        ignoreOrder: true,

        /*Specify the name for the css files generated and emitted by this plugin.Default is [name].css where name is the name of the entry point file(in my case name will be index).*/
        filename: "[name].[contenthash].css"
        
      }),
      new HtmlWebpackPlugin({
            title: "sample page generated using webpack",
            inject: "head",
            minify: true,
            template: "./index_template.html",
            fileName: "index.html"
          }),
    ],

});