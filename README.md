[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Clojars Project](https://img.shields.io/clojars/v/studiokalavati/bhatkhande-viewer.svg)](https://clojars.org/studiokalavati/bhatkhande-viewer)

# Bhatkhande Notation Canvas

This is an Clojurescript library that can display [sargam-spec](https://github.com/Studio-kalavati/sargam-spec) format annotated Hindustani classical music compositions in an HTML5 canvas. 

Here's an example of a part:
```json
{
	"m-noteseq": [
		[{
			"note": ["madhyam", "s"]
		}],
		[{
			"note": ["madhyam", "r"]
		}],
		[{
			"note": ["mandra", "-n"]
		}],
		[{
			"note": ["madhyam", "r"],
			"kan": ["madhyam", "-g"]
		}],
		[{
				"note": ["mandra", "n"]
			},
			{
				"note": ["madhyam", "s"]
			},
			{
				"note": ["madhyam", "g"]
			},
			{
				"note": ["madhyam", "m"]
			}
		],
		[{
			"note": ["madhyam", "g"]
		}],
		[{
			"note": ["madhyam", "m"]
		}],
		[{
				"note": ["madhyam", "r"]
			},
			{
				"note": ["madhyam", "r"]
			}
		],

		[{
			"note": ["madhyam", "s"]
		}]
	],

	"taal": {
		"num-beats": 10,
		"taal-name": "jhaptaal",
		"taal-label": "\u091d\u092a\u0924\u093e\u0932",
		"sam-khaali": {
			"1": "x",
			"3": "2",
			"8": "4",
			"6": "o"
		},
		"bhaags": [2, 3, 2, 3]
	},
	"part-label": "\u0938\u094d\u0925\u093e\u0907"
}
```

And the output is:

![sthayi](https://user-images.githubusercontent.com/89076/59617504-098f1680-9159-11e9-84d2-50c159569bc7.png)


## Development Mode

### Run application:

To view a sample composition, run

```
lein clean
lein figwheel dev
```

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).


## License

Copyright © 2019 Studio Kalavati

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.
